import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/api": {
        target: "http://backend:8080",  // NO SLASH AT END
        changeOrigin: true,
        secure: false
      }
    }
  }
});
