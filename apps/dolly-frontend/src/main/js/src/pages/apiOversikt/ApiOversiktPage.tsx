import React from 'react'
import { useApiOversikt } from '@/utils/hooks/useApiOversikt'
import { Box, InfoCard, Table } from '@navikt/ds-react'
import { ExclamationmarkTriangleFillIcon } from '@navikt/aksel-icons'
import Loading from '@/components/ui/loading/Loading'
import { SystemVisning } from '@/pages/apiOversikt/SystemVisning'

export default () => {
	const { apiOversikt, loading, error } = useApiOversikt()
	const systemer = apiOversikt?.data?.system

	return (
		<>
			<h1>API-oversikt</h1>
			<p>Her finner du en oversikt over alle eksisterende APIer som benyttes i Dolly.</p>
			<Box background="default" padding="space-16">
				{loading ? (
					<Loading label="Laster systemer ..." />
				) : error ? (
					<InfoCard data-color="warning">
						<InfoCard.Message
							icon={<ExclamationmarkTriangleFillIcon aria-hidden />}
						>{`Feil ved henting av data: ${error}`}</InfoCard.Message>
					</InfoCard>
				) : (
					<Table>
						<Table.Body>
							{systemer?.map((system: any) => {
								return (
									<Table.ExpandableRow
										key={system.register}
										content={<SystemVisning system={system} />}
									>
										<Table.HeaderCell>{system.register}</Table.HeaderCell>
									</Table.ExpandableRow>
								)
							})}
						</Table.Body>
					</Table>
				)}
			</Box>
		</>
	)
}
