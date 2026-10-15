import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router'
import permission from './directive/permission.js'

const app = createApp(App);
app.use(ElementPlus)
app.use(router)
app.directive('permission', permission)
app.mount('#app')