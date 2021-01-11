import React, { Fragment } from 'react'
import Formatters from '~/utils/DataFormatter'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { filterMiljoe } from '~/components/miljoVelger/MiljoeInfo/TilgjengeligeMiljoer'
import { GjenopprettModal } from './GjenopprettModal'

export default function GjenopprettBestilling(props) {
	const { bestilling, closeModal } = props
	const { environments } = bestilling

	const submitFormik = async values => {
		const envsQuery = Formatters.arrayToString(values.environments)
			.replace(/ /g, '')
			.toLowerCase()
		await props.gjenopprettBestilling(envsQuery)
		await props.getBestillinger()
	}

	const gjenopprettHeader = (
		<div style={{ paddingLeft: 20, paddingRight: 20 }}>
			<h1>Bestilling #{bestilling.id}</h1>
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
		/>
	)
}
