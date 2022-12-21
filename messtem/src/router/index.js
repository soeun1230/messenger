import Vue from 'vue'
import Router from 'vue-router'
import SignupForm from '@/components/SignupForm' // 로그인 컴포넌트를 import 한다

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/SignupForm', // 첫 화면을 로그인 화면으로 설정한다
      name: 'SignupForm',
      component: SignupForm
    }
  ]
})
