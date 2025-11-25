import { createContext, useContext, useReducer, useEffect } from "react";
import { authApi } from "../services/api";

const AuthContext = createContext();

const initialState = {
  user: null,
  isAuthenticated: false,
  isLoading: true,
  error: null,
};

const authReducer = (state, action) => {
  switch (action.type) {
    case "LOGIN_START":
      return { ...state, isLoading: true, error: null };

    case "LOGIN_SUCCESS":
      return {
        ...state,
        user: action.payload,
        isAuthenticated: true,
        isLoading: false,
        error: null,
      };

    case "LOGIN_FAILURE":
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: action.payload,
      };

    case "LOGOUT":
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
      };

    case "UPDATE_USER":
      return {
        ...state,
        user: { ...state.user, ...action.payload },
      };

    default:
      return state;
  }
};

export const AuthProvider = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Restore login session
  useEffect(() => {
    const token = localStorage.getItem("authToken");
    const userData = localStorage.getItem("userData");

    if (token && userData) {
      dispatch({ type: "LOGIN_SUCCESS", payload: JSON.parse(userData) });
    } else {
      dispatch({ type: "LOGOUT" });
    }
  }, []);

  // ============================
  // LOGIN
  // ============================
  const login = async (email, password) => {
    dispatch({ type: "LOGIN_START" });

    try {
      const res = await authApi.login(email, password);

      // Backend returns: { token, user }
      if (res.token) {
        localStorage.setItem("authToken", res.token);
        localStorage.setItem("userData", JSON.stringify(res.user));

        dispatch({ type: "LOGIN_SUCCESS", payload: res.user });
        return { success: true, user: res.user };
      }
      return { success: false, error: "Invalid login response" };
    } catch (err) {
      dispatch({
        type: "LOGIN_FAILURE",
        payload: err.message || "Login failed",
      });
      return { success: false, error: err.message };
    }
  };

  // ============================
  // REGISTER
  // ============================
  const register = async (name, email, password) => {
    dispatch({ type: "LOGIN_START" });

    try {
      const res = await authApi.register(name, email, password);

      // Backend returns: { token, user }
      if (res.token) {
        localStorage.setItem("authToken", res.token);
        localStorage.setItem("userData", JSON.stringify(res.user));

        dispatch({ type: "LOGIN_SUCCESS", payload: res.user });
        return { success: true, user: res.user };
      }

      return { success: false, error: "Invalid registration response" };
    } catch (err) {
      dispatch({
        type: "LOGIN_FAILURE",
        payload: err.message || "Register failed",
      });

      return { success: false, error: err.message };
    }
  };

  // ============================
  // LOGOUT
  // ============================
  const logout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userData");
    dispatch({ type: "LOGOUT" });
  };

  // ============================
  // UPDATE USER
  // ============================
  const updateUser = (userData) => {
    const updatedUser = { ...state.user, ...userData };
    localStorage.setItem("userData", JSON.stringify(updatedUser));
    dispatch({ type: "UPDATE_USER", payload: userData });
  };

  // ============================
  // PROFILE PICTURE UPDATE
  // ============================
  const updateProfilePicture = async (file) => {
    try {
      let imageUrl = null;

      if (file) {
        imageUrl = URL.createObjectURL(file);
      }

      const updatedUser = {
        ...state.user,
        avatar: imageUrl,
        profilePictureFile: file,
      };

      localStorage.setItem("userData", JSON.stringify(updatedUser));
      dispatch({
        type: "UPDATE_USER",
        payload: { avatar: imageUrl, profilePictureFile: file },
      });

      return { success: true, imageUrl };
    } catch (err) {
      return { success: false, error: err.message };
    }
  };

  return (
    <AuthContext.Provider
      value={{
        ...state,
        login,
        register,
        logout,
        updateUser,
        updateProfilePicture,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within an AuthProvider");
  return ctx;
};
