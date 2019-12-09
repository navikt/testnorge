import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'

export const Postadresser = ({ formikBag }) => (
	<Kategori title="Postadresse" vis="tpsf.postadresse">
		<FormikSelect name="tpsf.postadresse[0].postLand" label="Land" kodeverk="Landkoder" />
		<FormikTextInput name="tpsf.postadresse[0].postLinje1" label="Post 1" />
		<FormikTextInput name="tpsf.postadresse[0].postLinje2" label="Post 2" />
		<FormikTextInput name="tpsf.postadresse[0].postLinje3" label="Post 3" />
	</Kategori>
)
