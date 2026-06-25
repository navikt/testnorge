import React from 'react'

import FetchAccessToken from '@/components/FetchAccessToken'
import styled from 'styled-components'
import { Page } from '@/pages/Page'

const Content = styled.div`
	display: flex;
	justify-content: center;
`

export default () => (
	<Page>
		<Content>
			<FetchAccessToken
				scope="dev-gcp.dolly.dolly-auth-local"
				labels={{
					header: 'Magic Token',
					subHeader: 'Generer lokalt utviklingstoken',
					description: 'Dette token skal fungere for alle apper som kjøres lokalt.',
				}}
			/>
		</Content>
	</Page>
);