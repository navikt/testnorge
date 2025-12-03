import { Pagination, Table, VStack } from '@navikt/ds-react'
import { oversettBoolean } from '@/utils/DataFormatter'
import { IdentvalidatorVisning } from '@/pages/identvalidator/IdentvalidatorVisning'
import { useState } from 'react'

export const IdentvalidatorVisningTable = ({ identListe }) => {
	const [page, setPage] = useState(1)
	const rowsPerPage = 10

	if (!identListe || identListe.length === 0) {
		return null
	}

	let sortData = identListe
	sortData = sortData.slice((page - 1) * rowsPerPage, page * rowsPerPage)

	return (
		<VStack gap="space-16">
			<Table>
				<Table.Header>
					<Table.Row>
						<Table.HeaderCell />
						<Table.HeaderCell>Ident</Table.HeaderCell>
						<Table.HeaderCell>Er gyldig</Table.HeaderCell>
						<Table.HeaderCell>Er i prod</Table.HeaderCell>
						<Table.HeaderCell>Er ny ident (2032)</Table.HeaderCell>
						<Table.HeaderCell>Er syntetisk</Table.HeaderCell>
						<Table.HeaderCell>Er Testnorge-ident</Table.HeaderCell>
					</Table.Row>
				</Table.Header>
				<Table.Body>
					{sortData?.map((identData, index) => {
						return (
							<Table.ExpandableRow
								key={index + identData.ident}
								content={<IdentvalidatorVisning data={identData} />}
								defaultOpen={identListe.length === 1}
							>
								<Table.HeaderCell scope="row">{identData.ident}</Table.HeaderCell>
								<Table.DataCell>{oversettBoolean(identData.erGyldig)}</Table.DataCell>
								<Table.DataCell>{oversettBoolean(identData.erIProd)}</Table.DataCell>
								<Table.DataCell>{oversettBoolean(identData.erPersonnummer2032)}</Table.DataCell>
								<Table.DataCell>{oversettBoolean(identData.erSyntetisk)}</Table.DataCell>
								<Table.DataCell>{oversettBoolean(identData.erTestnorgeIdent)}</Table.DataCell>
							</Table.ExpandableRow>
						)
					})}
				</Table.Body>
			</Table>
			<Pagination
				page={page}
				onPageChange={setPage}
				count={Math.ceil(identListe?.length / rowsPerPage)}
				size="small"
			/>
		</VStack>
	)
}
