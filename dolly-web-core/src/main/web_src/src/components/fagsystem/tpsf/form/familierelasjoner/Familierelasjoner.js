import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { Partnere } from './partials/Partnere'
import { Barn } from './partials/Barn'

export const Familierelasjoner = ({ formikBag }) => (
	<Vis attributt="tpsf.relasjoner">
		<Panel
			heading="Familierelasjoner"
			hasErrors={panelError(formikBag)}
			iconType={'relasjoner'}
			startOpen
		>
			<Kategori title="Partnere" vis="tpsf.relasjoner.partnere">
				<Partnere formikBag={formikBag} />
			</Kategori>
			<Kategori title="Barn" vis="tpsf.relasjoner.barn">
				<Barn formikBag={formikBag} />
			</Kategori>
		</Panel>
	</Vis>
)
