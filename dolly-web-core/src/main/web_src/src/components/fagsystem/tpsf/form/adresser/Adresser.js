import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Boadresse } from './partials/boadresse/Boadresse'
import { MatrikkelAdresse } from './partials/MatrikkelAdresse'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export const Adresser = ({ formikBag }) => {
	return (
		<Panel heading="Adresser">
			{' '}
			{/* startOpen */}
			<Boadresse formikBag={formikBag} />
			<MatrikkelAdresse formikBag={formikBag} />
			<FormikDatepicker name="tpsf.boadresse.flyttedato" label="Flyttedato" />
		</Panel>
	)
}
