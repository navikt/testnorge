import React from 'react'
import { Jobbstatus } from '@/pages/adminPages/Levendearbeidsforhold/util/Typer'
import { InfoCard, VStack } from '@navikt/ds-react'
import { CheckmarkCircleFillIcon, InformationSquareFillIcon } from '@navikt/aksel-icons'

export const StatusBox = (data: Jobbstatus) => {
	const renderedBox = (status: boolean) => {
		if (!status) {
			return (
				<InfoCard data-color="info" style={{ marginBottom: '10px' }}>
					<InfoCard.Message icon={<InformationSquareFillIcon aria-hidden />}>
						Ingen aktiv jobb
					</InfoCard.Message>
				</InfoCard>
			)
		}
		return (
			<InfoCard data-color="success" style={{ marginBottom: '10px' }}>
				<InfoCard.Message icon={<CheckmarkCircleFillIcon aria-hidden />}>
					Aktiv jobb: Neste kjøring vil starte {data.nesteKjoring}
				</InfoCard.Message>
			</InfoCard>
		)
	}

	return <VStack gap="space-16">{renderedBox(data.status)}</VStack>
}
