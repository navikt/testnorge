import { Box, Button, Table } from '@navikt/ds-react'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { PencilWritingIcon } from '@navikt/aksel-icons'

export const AppstyringTable = ({ data }) => {
	return (
		<Box background="surface-default" padding="4">
			<ErrorBoundary>
				<Table>
					<Table.Header>
						<Table.HeaderCell>Parameter</Table.HeaderCell>
						<Table.HeaderCell>Verdi</Table.HeaderCell>
						<Table.HeaderCell>Rediger</Table.HeaderCell>
					</Table.Header>
					<Table.Body>
						{data.map((row, idx) => (
							<Table.Row key={row.parameter + idx}>
								<Table.DataCell width={'50%'}>{row.parameter}</Table.DataCell>
								<Table.DataCell width={'40%'}>{row.verdi}</Table.DataCell>
								<Table.DataCell width={'10%'}>
									<Button
										onClick={() => console.log('Redigerer...')}
										variant={'tertiary'}
										icon={<PencilWritingIcon />}
										size={'small'}
									/>
								</Table.DataCell>
							</Table.Row>
						))}
					</Table.Body>
				</Table>
			</ErrorBoundary>
		</Box>
	)
}
