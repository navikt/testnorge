import React from 'react'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'

export const TpsMessagingDiverse = () => (
	<div className="flexbox--flex-wrap">
		<FormikSelect
			name="tpsMessaging.spraakKode"
			label="Språk"
			kodeverk={PersoninformasjonKodeverk.Spraak}
			size="large"
			visHvisAvhuket
		/>
	</div>
)
