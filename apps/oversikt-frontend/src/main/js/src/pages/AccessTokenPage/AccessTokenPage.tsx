import React from 'react'
import PageWithMenu from '@/components/PageWithMenu'
import FetchAccessToken from '@/components/FetchAccessToken'
import { useParams } from 'react-router-dom'
import { Application } from '@/services/ApplicationService'

type Props = {
	navigations: Navigation<Application>[]
}

const AccessTokenPage = ({ navigations }: Props) => {
	// @ts-ignore
	const { name } = useParams()
	return (
		<PageWithMenu navigations={navigations} menuTitle="Applikasjoner">
			<FetchAccessToken
				scope={name}
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
