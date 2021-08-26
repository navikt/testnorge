import React from 'react'
import Pagination from 'paginering'
import DollyTable from '~/components/ui/dollyTable/DollyTable'
import { ManIconItem, WomanIconItem } from '~/components/ui/icon/IconItem'
import ContentContainer from '~/components/ui/contentContainer/ContentContainer'
import styled from 'styled-components'
import PersonView from '~/pages/testnorgePage/PersonView'
import { Pageing, Person } from '~/service/services/personsearch/types'

type Props = {
	items?: Person[]
	pageing: Pageing
	numberOfItems?: number
	onChange?: (page: number) => void
}

const SearchView = styled.div`
	display: flex;
	flex-direction: column;
`

const SearchPagination = styled(Pagination)`
	display: flex;
	align-self: center;
`

export default ({ items, pageing, numberOfItems, onChange }: Props) => {
	if (!items) {
		return <ContentContainer>Ingen resultat</ContentContainer>
	}

	const columns = [
		{
			text: 'Ident',
			width: '20',
			dataField: 'ident',
			unique: true
		},
		{
			text: 'Fornavn',
			width: '20',
			dataField: 'fornavn',
			unique: true
		},
		{
			text: 'Etternavn',
			width: '20',
			dataField: 'etternavn',
			unique: true
		},
		{
			text: 'Kjønn',
			width: '20',
			dataField: 'kjoenn'
		}
	]

	return (
		<SearchView>
			{/*@ts-ignore*/}
			<DollyTable
				data={items}
				columns={columns}
				iconItem={(person: Person) =>
					person?.kjoenn === 'MANN' ? <ManIconItem /> : <WomanIconItem />
				}
				onExpand={(person: Person) => <PersonView person={person} />}
			/>
			<SearchPagination
				currentPage={pageing.page}
				numberOfItems={numberOfItems || pageing.page * pageing.pageSize}
				itemsPerPage={pageing.pageSize}
				onChange={onChange}
			/>
		</SearchView>
	)
}
