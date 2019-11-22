import React from 'react'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Annet = ({ formikBag }) => (
	<Kategori title="Annet" vis={pathAttrs.kategori.annet}>
		<FormikSelect name="udistub.flyktning" label="Flyktningstatus" options={Options('boolean')} />
		<FormikSelect
			name="udistub.soeknadOmBeskyttelseUnderBehandling"
			label="Asylsøker"
			options={Options('jaNeiUavklart')}
		/>
	</Kategori>
)
