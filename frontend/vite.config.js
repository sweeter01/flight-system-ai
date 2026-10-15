import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const GATEWAY = 'http://localhost:80'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/auth-server': {
        target: GATEWAY,
        changeOrigin: true
      },
      '/addr-server': {
        target: GATEWAY,
        changeOrigin: true
      },
      '/flight-booking': {
        target: GATEWAY,
        changeOrigin: true
      },
      '/manager-server': {
        target: GATEWAY,
        changeOrigin: true
      },
      '/aircrew-server': {
        target: GATEWAY,
        changeOrigin: true
      },
      '/order-server': {
        target: GATEWAY,
        changeOrigin: true
      },
      '/flight/assistant': {
        target: GATEWAY,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/flight\/assistant/, '/flight-booking/flight/assistant')
      },
      '/addr/assistant': {
        target: GATEWAY,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/addr\/assistant/, '/addr-server/addr/assistant')
      },
      '/order/assistant': {
        target: GATEWAY,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/order\/assistant/, '/order-server/order/assistant')
      },
      '/manager/assistant': {
        target: GATEWAY,
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/manager\/assistant/, '/manager-server/manager/assistant')
      }
    }
  }
})