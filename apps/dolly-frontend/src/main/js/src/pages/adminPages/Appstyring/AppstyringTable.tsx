import { Box, Table } from '@navikt/ds-react'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

export const AppstyringTable = ({ data }) => {
	return (
		<Box background="surface-default" padding="4">
			<ErrorBoundary>
				<Table>
					<Table.Header>
						<Table.HeaderCell>Parameter</Table.HeaderCell>
						<Table.HeaderCell>Verdi</Table.HeaderCell>
					</Table.Header>
					<Table.Body>
						{data.map((row, idx) => (
							<Table.Row key={row.parameter + idx}>
								<Table.DataCell>{row.parameter}</Table.DataCell>
								<Table.DataCell>{row.verdi}</Table.DataCell>
							</Table.Row>
						))}
					</Table.Body>
				</Table>
			</ErrorBoundary>
		</Box>
	)
}
