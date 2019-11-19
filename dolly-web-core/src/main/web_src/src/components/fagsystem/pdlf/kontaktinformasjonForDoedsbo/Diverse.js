import React, { useState } from 'react'
import _get from 'lodash'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export const Diverse = ({ formikBag }) => {
	return (
		<Kategori title="Diverse">
			<FormikSelect name="kontaktinformasjonForDoedsbo.skifteform" label="Skifteform" />

			<FormikDatepicker
				name="kontaktinformasjonForDoedsbo.utstedtDato"
				label="Skifteform utstedt"
			/>
			<FormikDatepicker name="kontaktinformasjonForDoedsbo.gyldigFom" label="Gyldig fra" />
			<FormikDatepicker name="kontaktinformasjonForDoedsbo.gyldigTom" label="Gyldig til" />
		</Kategori>
	)
}
