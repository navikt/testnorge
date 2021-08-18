import React from 'react'
import _get from 'lodash'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { Adressat } from './Adressat'
import { Adresse } from './Adresse'

export const KontaktinformasjonForDoedsbo = ({ formikBag }) => {
	return (
		<React.Fragment>
			<Adressat formikBag={formikBag} />
			<Adresse formikBag={formikBag} />

			<Kategori title="Diverse">
				<FormikSelect
					name="pdlforvalter.kontaktinformasjonForDoedsbo.skifteform"
					label="Skifteform"
					options={Options('skifteform')}
					isClearable={false}
				/>
				<FormikDatepicker
					name="pdlforvalter.kontaktinformasjonForDoedsbo.utstedtDato"
					label="Skifteform utstedt"
				/>
			</Kategori>
		</React.Fragment>
	)
}
