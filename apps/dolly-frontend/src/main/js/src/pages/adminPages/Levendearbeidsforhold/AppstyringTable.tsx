import { Box, Table } from '@navikt/ds-react'
import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { EditParameter } from '@/pages/adminPages/Levendearbeidsforhold/EditParameter'
import { FetchData } from '@/pages/adminPages/Levendearbeidsforhold/util/Typer'

export const AppstyringTable = ({
	data,
	setData,
}: {
	data: Array<FetchData>
	setData: (data: Array<FetchData>) => void
}) => {
	return (
		<Box background="default" padding="space-16">
			<ErrorBoundary>
				<Table>
					<Table.Header>
						<Table.HeaderCell>Parameter</Table.HeaderCell>
						<Table.HeaderCell>Verdi</Table.HeaderCell>
						<Table.HeaderCell>Rediger</Table.HeaderCell>
					</Table.Header>
					<Table.Body>
						{data.map((row: FetchData, idx: number) => {
							return (
								<Table.Row key={row.navn + idx}>
									<Table.DataCell width={'50%'}>{row.tekst}</Table.DataCell>
									<Table.DataCell width={'40%'}>{row.verdi}</Table.DataCell>
									<Table.DataCell width={'10%'}>
										<EditParameter
											name={row.navn}
											label={row.tekst}
											initialValue={row.verdi}
											options={row.verdier}
											data={data}
											setData={setData}
										/>
									</Table.DataCell>
								</Table.Row>
							)
						})}
					</Table.Body>
				</Table>
			</ErrorBoundary>
		</Box>
	)
}
