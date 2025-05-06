import React from 'react'
import { useApiOversikt } from '@/utils/hooks/useApiOversikt'
import { Alert, Box, Table } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import { SystemVisning } from '@/pages/apiOversikt/SystemVisning'

export default () => {
	const { apiOversikt, loading, error } = useApiOversikt()
	const systemer = apiOversikt?.data?.system

	return (
		<>
			<h1>API-oversikt</h1>
			<p>Her finner du en oversikt over alle eksisterende APIer som benyttes i Dolly.</p>
			<Box background="surface-default" padding="4">
				{loading ? (
					<Loading label="Laster systemer ..." />
				) : error ? (
					<Alert variant="warning">{`Feil ved henting av data: ${error}`}</Alert>
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
