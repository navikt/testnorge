import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean, toTitleCase } from '@/utils/DataFormatter'
import { FullmaktType, Omraade } from '@/components/fagsystem/fullmakt/FullmaktType'
import React, { Fragment, useState } from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useFullmaktOmraader } from '@/utils/hooks/useFullmakt'
import Button from '@/components/ui/button/Button'

type FullmaktProps = {
	idx: number
	fullmakt: FullmaktType
}

export const Fullmakt = ({ fullmakt, idx }: FullmaktProps) => {
	const { omraadeKodeverk = [] } = useFullmaktOmraader()
	const [visOmraader, setVisOmraader] = useState(false)

	return (
		<Fragment key={idx}>
			<TitleValue title="Fullmektig" value={fullmakt?.fullmektig} />
			<TitleValue title="Fullmektigs navn" value={fullmakt?.fullmektigsNavn} />
			<TitleValue title="Gyldig fra" value={formatDate(fullmakt?.gyldigFraOgMed)} />
			<TitleValue title="Gyldig til" value={formatDate(fullmakt?.gyldigTilOgMed)} />
			<TitleValue title="Kilde" value={fullmakt?.kilde} />
			<TitleValue title="Opphørt" value={oversettBoolean(fullmakt?.opphoert)} />

			{fullmakt?.omraade?.length > 2 && (
				<Button
					onClick={() => setVisOmraader((prevState) => !prevState)}
					kind={visOmraader ? 'chevron-up' : 'chevron-down'}
				>
					{`${visOmraader ? 'SKJUL' : 'VIS'} OMRÅDER`}
				</Button>
			)}
			{(visOmraader || fullmakt?.omraade?.length <= 2) && (
				<DollyFieldArray
					style={{ marginTop: '10px' }}
					header={'Områder'}
					data={fullmakt?.omraade}
					nested
				>
					{(omraade: Omraade, idx: number) => {
						return (
							<React.Fragment>
								<div className="person-visning_content" key={idx}>
									<TitleValue title="Handling" value={toTitleCase(omraade?.handling?.join(', '))} />
									<TitleValue
										size={'xlarge'}
										title="Tema"
										value={omraadeKodeverk.find((option) => option.value === omraade.tema)?.label}
									/>
								</div>
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			)}
		</Fragment>
	)
}
