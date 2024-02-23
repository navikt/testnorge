import BlankHeader from '@/components/layout/blankHeader/BlankHeader'
import '../loginPage/LoginPage.less'
import { Background } from '@/components/ui/background/Background'
import styled from 'styled-components'

const FullPageH1 = styled.h1`
	height: calc(96vh - 71px);
	width: 100vw;
	overflow: hidden;
	margin-left: 20px;
`
export default () => {
	return (
		<>
			<BlankHeader />
			<Background>
				<FullPageH1>Du er nå logget inn, denne fanen kan lukkes</FullPageH1>
			</Background>
		</>
	)
}
