import { Alert, VStack } from '@navikt/ds-react'
import React from 'react'
import { Jobbstatus } from '@/pages/adminPages/Levendearbeidsforhold/util/Typer'

export const StatusBox = (data: Jobbstatus) => {
	const renderedBox = (status: boolean) => {
		if (!status) {
			return (
				<Alert variant="info" style={{ marginBottom: '10px' }}>
					Ingen aktiv jobb
				</Alert>
			)
		}
		return (
			<Alert variant="success" style={{ marginBottom: '10px' }}>
				Aktiv jobb: Neste kjøring vil starte {data.nesteKjoring}
			</Alert>
		)
	}

	return <VStack gap="space-16">{renderedBox(data.status)}</VStack>
}
