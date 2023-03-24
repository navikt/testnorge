import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

export const HjemmelForm = () => {
	const hjemmelPath = 'arbeidsplassenCV.harHjemmel'
	return (
		<Vis attributt={hjemmelPath}>
			<div className="dfa-blokk-nested_title">
				<h3>Hjemmel</h3>
				<Hjelpetekst>
					Hjemmel er ikke en del av CV, men brukes for å styre lesetilgang ved
					"rekrutteringsbistand" i mottakende system.
				</Hjelpetekst>
			</div>
			<div className="flexbox--full-width">
				<FormikCheckbox name={hjemmelPath} label="Godta hjemmel" size="small" />
			</div>
		</Vis>
	)
}
