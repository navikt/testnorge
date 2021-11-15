import React, { useState } from 'react'
import { Redirect } from 'react-router-dom'
import { DollyApi } from '~/service/Api'
import { VelgGruppeToggle } from './gruppe/VelgGruppeToggle'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'
import useBoolean from '~/utils/hooks/useBoolean'

interface ImportTilDollyModalProps {
	valgtePersoner: Array<string>
	onAvbryt: () => void
}

const KILDE_MILJOE = 'q2'
const DOLLY_MILJOE = ['q2']

export const ImportTilDollyModal = ({ valgtePersoner, onAvbryt }: ImportTilDollyModalProps) => {
	const [valgtGruppe, setValgtGruppe] = useState('')
	const [feilmelding, setFeilmelding] = useState('')
	const [redirectToGruppe, setRedirect] = useBoolean()

	const onSubmit = () => {
		const request = {
			identer: valgtePersoner,
			kildeMiljoe: KILDE_MILJOE,
			environments: DOLLY_MILJOE,
		}

		DollyApi.importerPersoner(valgtGruppe, request).then((response: any) => {
			if (response.error) {
				const msg = response.message || 'Noe gikk galt'
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
				endringer på dem. Personen(e) ligger i Q2.
			</h4>
			<VelgGruppeToggle valgtGruppe={valgtGruppe} setValgtGruppe={setValgtGruppe} />
			{feilmelding && <div className="error-message">{feilmelding}</div>}
			<ModalActionKnapper
				submitknapp="Importer"
				submitTitle={valgtGruppe.length < 1 ? 'Velg en gruppe' : null}
				disabled={valgtGruppe.length < 1}
				onAvbryt={onAvbryt}
				onSubmit={onSubmit}
				center
			/>
		</div>
	)
}
