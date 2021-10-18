import React from 'react'

import { Page } from '@/pages/Page'
import styled from 'styled-components'
import ChooseBox from '@/pages/UserPage/ChooseBox'
import OrgTableBox from '@/pages/UserPage/OrgTableBox'
import UserInfoBox from '@/pages/UserPage/UserInfoBox'
import CreateUserBox from '@/pages/UserPage/CreateUserBox'
import DeleteUserBox from '@/pages/UserPage/DeleteUserBox'
import ChangeUsernameBox from '@/pages/UserPage/ChangeUsernameBox'

const Content = styled.div`
	display: flex;
	justify-content: center;
	flex-flow: wrap;
`

export default () => (
	<Page>
		<Content>
			<OrgTableBox />
			<UserInfoBox />
			<ChooseBox />
			<CreateUserBox />
			<DeleteUserBox />
			<ChangeUsernameBox />
		</Content>
	</Page>
)
