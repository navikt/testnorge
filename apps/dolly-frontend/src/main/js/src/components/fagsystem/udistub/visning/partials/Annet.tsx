import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { oversettBoolean, showLabel } from '@/utils/DataFormatter'

export const Annet = ({ data }) => {
	const { flyktning, soeknadOmBeskyttelseUnderBehandling } = data
	if (!flyktning && !soeknadOmBeskyttelseUnderBehandling) {
		return null
	}

	return (
		<div>
			<h4>Annet</h4>
			<div className="person-visning_content">
				<TitleValue title="Har flyktningstatus" value={oversettBoolean(flyktning)} />
				<TitleValue
					title="Er asylsøker"
					value={showLabel('jaNeiUavklart', soeknadOmBeskyttelseUnderBehandling)}
				/>
			</div>
		</div>
	)
}
