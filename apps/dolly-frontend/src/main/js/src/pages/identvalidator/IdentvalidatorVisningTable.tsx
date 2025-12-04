import { Alert, Box, HStack, Pagination, Table, VStack } from '@navikt/ds-react'
import { oversettBoolean } from '@/utils/DataFormatter'
import { getIcon, IdentvalidatorVisning } from '@/pages/identvalidator/IdentvalidatorVisning'
import { useState } from 'react'
import { isBoolean } from 'lodash-es'

function comparator<T>(a: T, b: T, orderBy: keyof T): number {
	if (b[orderBy] == null || b[orderBy] < a[orderBy]) {
		return -1
	}
	if (b[orderBy] > a[orderBy]) {
		return 1
	}
	return 0
}

const IconItem = (isValid: boolean, iconType: string) => {
	if (!isBoolean(isValid)) return null
	return iconType === 'none' ? (
		<HStack gap="space-16">
			<div style={{ width: '20px', textAlign: 'center' }}>-</div>
			{oversettBoolean(isValid)}
		</HStack>
	) : (
		<Alert variant={iconType} inline>
			{oversettBoolean(isValid)}
		</Alert>
	)
}

export const IdentvalidatorVisningTable = ({ identListe }) => {
	const [page, setPage] = useState(1)
	const rowsPerPage = 10

	const [sort, setSort] = useState({ orderBy: 'erIProd', direction: 'ascending' })

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
		<Box background={'surface-default'} padding="6">
			<VStack gap="space-16">
				<Table sort={sort} onSortChange={(sortKey) => handleSort(sortKey)}>
					<Table.Header>
						<Table.Row>
							<Table.HeaderCell />
							<Table.HeaderCell scope="col">Ident</Table.HeaderCell>
							<Table.ColumnHeader sortKey="erGyldig" sortable>
								Gyldig
							</Table.ColumnHeader>
							<Table.ColumnHeader sortKey="erIProd" sortable>
								I prod
							</Table.ColumnHeader>
							<Table.ColumnHeader sortKey="erSyntetisk" sortable>
								Syntetisk
							</Table.ColumnHeader>
							<Table.ColumnHeader sortKey="erTestnorgeIdent" sortable>
								Tenor-ident
							</Table.ColumnHeader>
							<Table.ColumnHeader sortKey="erPersonnummer2032" sortable>
								Ny ident (2032)
							</Table.ColumnHeader>
						</Table.Row>
					</Table.Header>
					<Table.Body>
						{sortedData?.map((identData, index) => {
							return (
								<Table.ExpandableRow
									key={index + identData.ident}
									content={<IdentvalidatorVisning data={identData} />}
									defaultOpen={identListe.length === 1}
									expandOnRowClick
								>
									<Table.HeaderCell scope="row" width="18%">
										{identData.ident}
									</Table.HeaderCell>
									<Table.DataCell>
										{IconItem(identData.erGyldig, getIcon(identData.erGyldig, true))}
									</Table.DataCell>
									<Table.DataCell>
										{IconItem(identData.erIProd, identData.erIProd ? 'warning' : 'none')}
									</Table.DataCell>
									<Table.DataCell>
										{IconItem(identData.erSyntetisk, getIcon(identData.erSyntetisk))}
									</Table.DataCell>
									<Table.DataCell>
										{IconItem(identData.erTestnorgeIdent, getIcon(identData.erTestnorgeIdent))}
									</Table.DataCell>
									<Table.DataCell>
										{IconItem(identData.erPersonnummer2032, getIcon(identData.erPersonnummer2032))}
									</Table.DataCell>
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
		</Box>
	)
}
