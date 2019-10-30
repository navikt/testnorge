import React, { useState } from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { GyldigAdresseVelger } from './GyldigAdresseVelger/GyldigAdresseVelger'

export const Boadresse = ({ formikProps }) => {
	const settBoadresse = (adresse, husnr) => {
		// sett verdier i formik
		// formikProps.form.setFieldValue('')
		console.log(adresse, husnr)
	}

	return (
		<Kategori title="Boadresse">
			<div style={{ width: '100%' }}>
				<GyldigAdresseVelger settBoadresse={settBoadresse} />
				<p>Vis adresse fra formik her</p>
			</div>
		</Kategori>
	)
}
