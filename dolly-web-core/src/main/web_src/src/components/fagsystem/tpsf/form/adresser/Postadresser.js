import React from 'react'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'

export const Postadresser = ({ formikBag }) => (
	<Vis attributt={pathAttrs.kategori.postadresse}>
		<Panel heading="Postadresse" hasErrors={panelError(formikBag)}>
			<FormikSelect name="postAdresse.land" label="Land" kodeverk="Landkoder" />
			<FormikTextInput name="tpsf.postadresse" label="Post 1" />
			<FormikTextInput name="tpsf.postadresse" label="Post 2" />
			<FormikTextInput name="tpsf.postadresse" label="Post 3" />
		</Panel>
	</Vis>
)
