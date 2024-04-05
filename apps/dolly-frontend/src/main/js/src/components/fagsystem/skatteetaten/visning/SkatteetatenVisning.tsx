import React from 'react'
import Loading from '@/components/ui/loading/Loading'
import Icon from '@/components/ui/icon/Icon'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'

type SkatteetatenVisningProps = {
	data: {
		tenorRelasjoner: string[]
	}
	loading: boolean
}

// Midlertidig visning av daglig leder-rolle fra Tenor. Skrives om naar det er behov for aa vise flere Tenor-data.
export const SkatteetatenVisning = ({ data, loading }: SkatteetatenVisningProps) => {
	if (loading) {
		return <Loading label="Laster Tenor-data ..." />
	}

	const harDagligLederRolle = data?.tenorRelasjoner?.includes('BrregErFr')

	if (!data || !harDagligLederRolle) {
		return null
	}

	return (
		<div style={{ marginTop: '15px' }}>
			<div className="sub-overskrift" style={{ backgroundColor: '#4B797A', color: '#fff' }}>
				<Icon fontSize={'1.5rem'} kind="tenor" />
				<h3>Data fra Tenor</h3>
			</div>
			<h4 style={{ margin: '10px 0' }}>Enhetsregisteret og Foretaksregisteret</h4>
			<div className="person-visning_content">
				<TitleValue title="Roller" value="Daglig leder" />
			</div>
		</div>
	)
}
