import Title from '@/components/Title'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { bottom } from '@popperjs/core'

export default () => {
	return (
		<div>
			<div className="flexbox--align-center--justify-start">
				<Title title="Søk etter personer i Tenor" />
				<Hjelpetekst placement={bottom}>Blablablah</Hjelpetekst>
			</div>
			<p>Test</p>
		</div>
	)
}
