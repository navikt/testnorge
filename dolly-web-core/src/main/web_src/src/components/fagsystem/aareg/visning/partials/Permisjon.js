import React from 'react'
import Loading from '~/components/ui/loading/Loading'

export const Permisjon = ({ data, loading }) => {
	if (loading) return <Loading label="laster Aareg-data" />
	if (!data) return false

	return (
		<div>
			<div className="person-visning_content">
				{data.map((id, idx) => (
					<div>
						{/* 						 permisjonOgPermittering
						 permisjonsId
						 permisjonsperiode (fom og tom)
						 permisjonsprosent
						 arbeidsforhold*/}
					</div>
				))}
			</div>
		</div>
	)
}
