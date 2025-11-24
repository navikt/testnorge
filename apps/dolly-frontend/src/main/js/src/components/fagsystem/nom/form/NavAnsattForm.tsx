import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { NomForm } from '@/components/fagsystem/nom/form/NomForm'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import React from 'react'
import { useFormContext } from 'react-hook-form'
import { SkjermingForm } from '@/components/fagsystem/skjermingsregister/form/SkjermingForm'

const nomdataPath = ['nomdata']
const skjermingPaths = ['skjerming.egenAnsattDatoFom', 'skjerming.egenAnsattDatoTom']
const panelPaths = [nomdataPath, skjermingPaths].flat()

export const NavAnsatt = () => {
	const formMethods = useFormContext()

	return (
		<Vis attributt={panelPaths}>
			<Panel
				heading="Nav-ansatt"
				hasErrors={panelError(panelPaths)}
				iconType="nav"
				startOpen={
					erForsteEllerTest(formMethods.getValues(), nomdataPath) ||
					erForsteEllerTest(formMethods.getValues(), skjermingPaths)
				}
			>
				<Kategori title="Nav-ident (NOM)" vis={nomdataPath}>
					<NomForm />
				</Kategori>
				<Kategori title="Skjerming" vis={skjermingPaths}>
					<SkjermingForm />
				</Kategori>
			</Panel>
		</Vis>
	)
}
