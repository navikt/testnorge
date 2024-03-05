import { useFinnesIDolly } from '@/utils/hooks/useIdent'
import { NavigerTilPerson } from '@/pages/tenorSoek/resultatVisning/NavigerTilPerson'
import { ImporterPerson } from '@/pages/tenorSoek/resultatVisning/ImporterPerson'
import { Checkbox } from '@navikt/ds-react'

export const ListeValg = ({ ident, isMultiple = false, markertePersoner, setMarkertePersoner }) => {
	const { finnesIDolly, loading: loadingFinnes, error: errorFinnes } = useFinnesIDolly(ident)
	// console.log('finnesIDolly: ', finnesIDolly) //TODO - SLETT MEG

	const handleChangeCheckbox = (val: any[]) => {
		if (val?.target?.checked) {
			setMarkertePersoner([...markertePersoner, val?.target?.value])
		} else {
			setMarkertePersoner(markertePersoner.filter((ident) => ident !== val?.target?.value))
		}
	}

	return finnesIDolly ? (
		<NavigerTilPerson ident={ident} />
	) : isMultiple ? (
		<Checkbox value={ident} size="small" onChange={(val: any) => handleChangeCheckbox(val)}>
			Importer til gruppe
		</Checkbox>
	) : (
		<ImporterPerson ident={ident} />
	)
}
