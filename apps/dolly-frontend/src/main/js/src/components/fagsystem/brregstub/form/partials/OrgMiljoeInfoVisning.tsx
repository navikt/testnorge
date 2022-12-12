import React from 'react'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'

type Props = {
	miljoer: string[]
	loading?: boolean
	error?: boolean
}

export const OrgMiljoeInfoVisning = ({ miljoer, loading = false, error = false }: Props) => {
	const harMiljoe = miljoer.length > 0
	return (
		<div style={{ padding: '0 0 10px 5px' }}>
			{loading && <Loading label="Sjekker organisasjonsnummer..." />}
			{!loading && error && (
				<div className="flexbox">
					<Icon size={20} kind="report-problem-circle" />
					Feil oppsto i henting av organisasjon info
				</div>
			)}
			{!loading && !error && (
				<div className="flexbox">
					<Icon
						size={20}
						kind={harMiljoe ? 'feedback-check-circle' : 'report-problem-circle'}
						style={{ marginRight: '5px' }}
					/>
					{harMiljoe
						? 'Organisasjon funnet i miljø: ' + miljoer
						: 'Fant ikke organisasjon i noen miljø'}
				</div>
			)}
		</div>
	)
}
