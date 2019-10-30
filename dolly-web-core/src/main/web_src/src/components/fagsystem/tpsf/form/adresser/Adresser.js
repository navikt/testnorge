import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { Boadresse } from './partials/boadresse/Boadresse'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

export const Adresser = ({ formikProps }) => {
	return (
		<Panel heading="Adresser" startOpen>
			<Boadresse formikProps={formikProps} />
			<FormikDatepicker name="tpsf.boadresse.flyttedato" label="Flyttedato" />
		</Panel>
	)
}
