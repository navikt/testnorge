import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'

export const ArbeidsplassenVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster CV" />
	if (!data) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Arbeidsplassen (CV)" iconKind="cv" />
			{/*//TODO: lag kategorier!*/}
		</>
	)
}
