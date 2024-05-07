import { useFinnesIDolly } from '@/utils/hooks/useIdent'
import { NavigerTilPerson } from '@/pages/tenorSoek/resultatVisning/NavigerTilPerson'
import { Checkbox } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'

type ListeValgProps = {
	ident: string
	markertePersoner: Array<string>
	setMarkertePersoner: Function
}

export const ListeValg = ({ ident, markertePersoner, setMarkertePersoner }: ListeValgProps) => {
	const { finnesIDolly, loading: loadingFinnes } = useFinnesIDolly(ident)

	if (loadingFinnes) {
		return <Loading onlySpinner />
	}

	const handleChangeCheckbox = (val: any) => {
		if (val?.target?.checked) {
			setMarkertePersoner([...markertePersoner, val?.target?.value])
		} else {
			setMarkertePersoner(markertePersoner.filter((ident) => ident !== val?.target?.value))
		}
	}

	return finnesIDolly ? (
		<NavigerTilPerson ident={ident} />
	) : (
		<div style={{ margin: '-8px 0' }}>
			<Checkbox
				value={ident}
				size="small"
				onChange={(val: any) => handleChangeCheckbox(val)}
				onClick={(e) => e.stopPropagation()}
			>
				Importer person
			</Checkbox>
		</div>
	)
}
