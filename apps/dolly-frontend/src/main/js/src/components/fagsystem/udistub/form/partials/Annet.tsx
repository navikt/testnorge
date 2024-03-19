import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

export const Annet = () => (
	<Kategori
		title="Annet"
		vis={['udistub.flyktning', 'udistub.soeknadOmBeskyttelseUnderBehandling']}
	>
		<div className="flexbox--flex-wrap">
			<Vis attributt="udistub.flyktning">
				<FormSelect
					name="udistub.flyktning"
					label="Flyktningstatus"
					options={Options('boolean')}
					isClearable={false}
				/>
			</Vis>
			<Vis attributt="udistub.soeknadOmBeskyttelseUnderBehandling">
				<FormSelect
					name="udistub.soeknadOmBeskyttelseUnderBehandling"
					label="Asylsøker"
					options={Options('jaNeiUavklart')}
					isClearable={false}
				/>
			</Vis>
		</div>
	</Kategori>
)
