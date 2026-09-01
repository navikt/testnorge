import React from 'react'
import PageWithMenu from '@/components/PageWithMenu'
import type { AppNavigationItem } from '@/components/Navigation'
import FetchAccessToken from '@/components/FetchAccessToken'
import { useParams } from 'react-router'
import { Application } from '@/services/ApplicationService'

type Props = {
	navigations: AppNavigationItem<Application>[]
}

const AccessTokenPage = ({ navigations }: Props) => {
	const { name } = useParams<{ name: string }>()
	return (
		<PageWithMenu navigations={navigations} menuTitle="Applikasjoner">
			<FetchAccessToken
				scope={name ?? ''}
				labels={{
					header: 'Access Token',
					subHeader: `Generer token for ${name}`,
					description: `Token som kan brukes til å logge på ${name}.`,
				}}
			/>
		</PageWithMenu>
	)
}

export default AccessTokenPage
