import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { Boadresse } from './partials/boadresse/Boadresse'
import { MatrikkelAdresse } from './partials/MatrikkelAdresse'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

const paths = [pathAttrs.kategori.boadresse, pathAttrs.kategori.postadresse].flat()

export const Adresser = ({ formikBag }) => {
	const [type, setOn, setOff] = useBoolean(true)

	return (
		<Vis attributt={paths}>
			<Panel heading="Adresser" startOpen>
				<Vis attributt="tpsf.boadresse">
					<button onClick={setOn}>Gateadresse</button>
					<button onClick={setOff}>Matrikkeladresse</button>
					{type && <Boadresse formikBag={formikBag} />}
					{!type && <MatrikkelAdresse formikBag={formikBag} />}
					<FormikDatepicker name="tpsf.boadresse.flyttedato" label="Flyttedato" />
				</Vis>
				<Vis attributt="tpsf.boadresse">
					<span>postadresse komponent</span>
				</Vis>
			</Panel>
		</Vis>
	)
}
