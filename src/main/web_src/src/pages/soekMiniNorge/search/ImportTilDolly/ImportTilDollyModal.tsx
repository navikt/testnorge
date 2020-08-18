import React, { useState } from 'react'
import { Redirect } from 'react-router-dom'
import { api } from './gruppe/api'
import { VelgGruppeToggle } from './gruppe/VelgGruppeToggle'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'
import useBoolean from '~/utils/hooks/useBoolean'

interface ImportTilDollyModal {
	valgtePersoner: Array<string>
	onAvbryt: () => void
}

const KILDE_MILJOE = 'q2'
const DOLLY_MILJOE = ['q2']

export const ImportTilDollyModal = ({ valgtePersoner, onAvbryt }: ImportTilDollyModal) => {
	const [valgtGruppe, setValgtGruppe] = useState('')
	const [feilmelding, setFeilmelding] = useState('')
	const [redirectToGruppe, setRedirect, ikkeRedirect] = useBoolean()

	const onSubmit = () => {
		const request = {
			identer: valgtePersoner,
			kildeMiljoe: KILDE_MILJOE,
			environments: DOLLY_MILJOE
		}

		api.importerPersoner(valgtGruppe, request).then(response => {
			if (!response.ok) {
				const msg = response.statusText || 'Noe gikk galt'
				setFeilmelding(msg)
			} else {
				setRedirect()
				onAvbryt()
			}
		})
	}

	if (redirectToGruppe) return <Redirect to={`/gruppe/${valgtGruppe}`} />

	return (
		<div className="flexbox--column">
			<h1>Importer {valgtePersoner.length} person(er) til gruppe</h1>
			<h4>
				Velg en testdatagruppe du ønsker å importere personen(e) til, eller opprett en ny
				testdatagruppe. Du vil da finne personen(e) i valgt gruppe, og kan bruke Dolly til å gjøre
				endringer på dem.
			</h4>
			<VelgGruppeToggle valgtGruppe={valgtGruppe} setValgtGruppe={setValgtGruppe} />
			{feilmelding && <div className="error-message">{feilmelding}</div>}
			<ModalActionKnapper
				submitknapp="Importer"
				submitTitle={valgtGruppe.length < 1 ? 'Velg en gruppe' : null}
				disabled={valgtGruppe.length < 1}
				onAvbryt={onAvbryt}
				onSubmit={() => onSubmit()}
				center
			/>
		</div>
	)
}
