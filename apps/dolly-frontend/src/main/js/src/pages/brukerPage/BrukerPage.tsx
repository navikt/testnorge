import BlankHeader from '@/components/layout/blankHeader/BlankHeader'
import BrukerModal from '@/pages/brukerPage/BrukerModal'
import './BrukerPage.less'
import { Background } from '@/components/ui/background/Background'

export default () => {
	return (
		<>
			<BlankHeader />
			<Background>
				<BrukerModal />
			</Background>
		</>
	)
}
