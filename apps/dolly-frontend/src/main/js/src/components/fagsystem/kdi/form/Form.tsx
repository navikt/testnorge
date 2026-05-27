import { useFormContext } from 'react-hook-form'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { instdataKdiAttributt } from '@/components/fagsystem/kdi/initialValues'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, usePanelError } from '@/components/ui/form/formUtils'
import React from 'react'
import { InnsettelseForm } from '@/components/fagsystem/kdi/form/partials/InnsettelseForm'
import { LoeslatelseForm } from '@/components/fagsystem/kdi/form/partials/LoeslatelseForm'

export const KdiForm = () => {
	const formMethods = useFormContext()
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const { personFoerLeggTil }: any = opts
	const eksisterendeKdiData = personFoerLeggTil?.instdataKdi

	return (
		<Vis attributt={instdataKdiAttributt}>
			<Panel
				heading={'KDI-meldinger'}
				startOpen={erForsteEllerTest(formMethods.getValues(), [instdataKdiAttributt])}
				hasErrors={usePanelError(instdataKdiAttributt)}
				iconType="institusjon"
			>
				<InnsettelseForm eksisterendeKdiData={eksisterendeKdiData} />
				<LoeslatelseForm eksisterendeKdiData={eksisterendeKdiData} />
			</Panel>
		</Vis>
	)
}
