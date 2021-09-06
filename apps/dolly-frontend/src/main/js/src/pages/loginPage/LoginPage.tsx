import React from 'react'
import styled from 'styled-components'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import LoginModal from '~/pages/loginPage/LoginModal'
import './LoginPage.less'
// @ts-ignore
import Summer from '~/pages/loginPage/backgrounds/Summer.svg'

const Container = styled.div`
	display: flex;
`

const Background = styled.div`
    background-image: url(data:image/svg+xml;base64,${btoa(Summer)});
    background-size: cover;
    background-position: bottom;
	display: flex;
	flex: 1;
`

export default () => {
	return (
		<React.Fragment>
			<BlankHeader />
			<Container>
				<Background>
					<LoginModal />
				</Background>
			</Container>
		</React.Fragment>
	)
}
