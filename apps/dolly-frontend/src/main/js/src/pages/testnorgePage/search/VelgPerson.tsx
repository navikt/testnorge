import { Checkbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { ImportPerson } from '@/pages/testnorgePage/search/SearchView'
import { PdlData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import styled from 'styled-components'
import React from 'react'

interface VelgPersonProps {
	ident: string
	data: PdlData
	valgtePersoner: Array<ImportPerson>
	setValgtePersoner: (personer: Array<ImportPerson>) => void
}

const CenteredDiv = styled.div`
	text-align: -webkit-center;
`

export const VelgPerson = ({ ident, data, valgtePersoner, setValgtePersoner }: VelgPersonProps) => {
	const avhuket = valgtePersoner.map((person) => person?.ident).includes(ident)

	const handleOnChange = () => {
		if (avhuket) {
			setValgtePersoner(valgtePersoner.filter((valgtident) => valgtident.ident !== ident))
		} else {
			setValgtePersoner([...valgtePersoner, { ident: ident, data: data }])
		}
	}

	return (
		<CenteredDiv
			onClick={(event: React.MouseEvent<any>) => {
				event.stopPropagation()
			}}
		>
			<Checkbox id={ident} title={'Marker'} checked={avhuket} onChange={handleOnChange} />
		</CenteredDiv>
	)
}
