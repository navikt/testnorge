import { Pagination, Table, VStack } from '@navikt/ds-react'
import { oversettBoolean } from '@/utils/DataFormatter'
import { IdentvalidatorVisning } from '@/pages/identvalidator/IdentvalidatorVisning'
import { useState } from 'react'

function comparator<T>(a: T, b: T, orderBy: keyof T): number {
	if (b[orderBy] == null || b[orderBy] < a[orderBy]) {
		return -1
	}
	if (b[orderBy] > a[orderBy]) {
		return 1
	}
	return 0
}

export const IdentvalidatorVisningTable = ({ identListe }) => {
	const [page, setPage] = useState(1)
	const rowsPerPage = 10

	const [sort, setSort] = useState()

	if (!identListe || identListe.length === 0) {
		return null
	}

	const handleSort = (sortKey) => {
		setSort(
			sort && sortKey === sort.orderBy && sort.direction === 'descending'
				? undefined
				: {
						orderBy: sortKey,
						direction:
							sort && sortKey === sort.orderBy && sort.direction === 'ascending'
								? 'descending'
								: 'ascending',
					},
		)
	}

	let sortedData = identListe.slice().sort((a, b) => {
		if (sort) {
			return sort.direction === 'descending'
				? comparator(b, a, sort.orderBy)
				: comparator(a, b, sort.orderBy)
		}
		return 1
	})
	sortedData = sortedData.slice((page - 1) * rowsPerPage, page * rowsPerPage)

	return (
		<VStack gap="space-16">
			<Table sort={sort} onSortChange={(sortKey) => handleSort(sortKey)}>
				<Table.Header>
					<Table.Row>
						<Table.HeaderCell />
						<Table.HeaderCell scope="col">Ident</Table.HeaderCell>
						<Table.HeaderCell scope="col">Er gyldig</Table.HeaderCell>
						<Table.ColumnHeader sortKey="erIProd" sortable>
							Er i prod
						</Table.ColumnHeader>
						<Table.HeaderCell scope="col">Er ny ident (2032)</Table.HeaderCell>
						<Table.HeaderCell scope="col">Er syntetisk</Table.HeaderCell>
						<Table.HeaderCell scope="col">Er Testnorge-ident</Table.HeaderCell>
					</Table.Row>
				</Table.Header>
				<Table.Body>
					{sortedData?.map((identData, index) => {
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
