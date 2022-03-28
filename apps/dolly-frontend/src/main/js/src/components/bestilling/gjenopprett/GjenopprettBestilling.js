import React from 'react'
import Formatters from '~/utils/DataFormatter'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { GjenopprettModal } from './GjenopprettModal'

export default function GjenopprettBestilling(props) {
	const { bestilling, closeModal, brukertype } = props
	const { environments } = bestilling
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

	const submitFormik = async (values) => {
		const envsQuery = Formatters.arrayToString(values.environments).replace(/ /g, '').toLowerCase()
		erOrganisasjon
			? await props.gjenopprettOrganisasjonBestilling(envsQuery)
			: await props.gjenopprettBestilling(envsQuery)
		await props.getBestillinger()
		closeModal()
	}

	const gjenopprettHeader = (
		<div style={{ paddingLeft: 20, paddingRight: 20 }}>
			<h1>Gjenopprett bestilling #{bestilling.id}</h1>
			<br />
			<TitleValue title="Bestilt miljø" value={Formatters.arrayToString(environments)} />
			<hr />
		</div>
	)

	return (
		<GjenopprettModal
			gjenopprettHeader={gjenopprettHeader}
			environments={environments}
			submitFormik={submitFormik}
			closeModal={closeModal}
			bestilling={bestilling}
			brukertype={brukertype}
		/>
	)
}
