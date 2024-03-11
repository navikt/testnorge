import React from 'react'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'

export const TpsMessagingDiverse = () => (
	<div className="flexbox--flex-wrap">
		<FormSelect
			name="tpsMessaging.spraakKode"
			label="Språk"
			kodeverk={PersoninformasjonKodeverk.Spraak}
			size="large"
			visHvisAvhuket
		/>
	</div>
)
