import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Diverse = ({ formikBag }) => {
	return (
		<Kategori title="Diverse">
			<FormikSelect name="udistub.flyktning" label="Flyktningstatus" options={Options('boolean')} />
			<FormikSelect
				name="udistub.soeknadOmBeskyttelseUnderBehandling"
				label="Asylsøker"
				options={Options('jaNeiUavklart')}
			/>
		</Kategori>
	)
}
