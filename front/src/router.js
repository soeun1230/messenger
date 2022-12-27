import { createRouter, createWebHashHistory } from 'vue-router';
import TheHome from './components/TheHome.vue';
import TheRegistration from './components/TheRegistration.vue';
import TheLogin from './components/TheLogin.vue';
import UserDash from './components/UserDash.vue';
import GiftList from './components/GiftList.vue';

const router=createRouter({
    history:createWebHashHistory(),
    routes:[
        {path:"/",redirect:'/home'},

        {path:"/home",component:TheHome},

        {path:"/registration",component:TheRegistration},
        {path:"/login",component:TheLogin},

        {path:"/user",component:UserDash},

        {path:"/gift",component:GiftList}
    ]
})

export default router;