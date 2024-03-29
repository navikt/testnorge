import BlankHeader from '@/components/layout/blankHeader/BlankHeader'
import LoginModal from '@/pages/loginPage/LoginModal'
import './LoginPage.less'
import { Background } from '@/components/ui/background/Background'

export default () => {
	return (
		<>
			<BlankHeader />
			<Background>
				<LoginModal />
			</Background>
		</>
	)
}
