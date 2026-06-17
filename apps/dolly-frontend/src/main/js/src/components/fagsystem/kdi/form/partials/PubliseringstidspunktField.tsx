import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import React, { useEffect, useRef } from 'react'
import { useFormContext } from 'react-hook-form'
import { naaPubliseringstidspunkt } from '@/components/fagsystem/kdi/form/Form'
import { Button } from '@navikt/ds-react'
import { ArrowsCirclepathIcon } from '@navikt/aksel-icons'

type PubliseringstidspunktFieldProps = {
	path: string
	erEksisterendeMelding: boolean
	onSort?: () => void
	sortVersjon?: number
}

export const PubliseringstidspunktField = ({
	path,
	erEksisterendeMelding,
	onSort,
	sortVersjon,
}: PubliseringstidspunktFieldProps) => {
	const formMethods = useFormContext()
	const publiseringsTidspunktPath = `${path}.publiseringstidspunkt`
	const publiseringstidspunkt = formMethods.watch(publiseringsTidspunktPath)

	const sortertTidspunkt = useRef(publiseringstidspunkt)
	const erFoersteRender = useRef(true)

	useEffect(() => {
		if (!publiseringstidspunkt) {
			const naa = naaPubliseringstidspunkt()
			formMethods.setValue(publiseringsTidspunktPath, naa)
			sortertTidspunkt.current = naa
		}
	}, [])

	useEffect(() => {
		if (erFoersteRender.current) {
			erFoersteRender.current = false
			return
		}
		sortertTidspunkt.current = formMethods.getValues(publiseringsTidspunktPath)
	}, [sortVersjon])

	const erEndret = !!onSort && publiseringstidspunkt !== sortertTidspunkt.current

	const handleSort = () => {
		sortertTidspunkt.current = publiseringstidspunkt
		onSort?.()
	}

	return (
		<div style={{ display: 'flex' }}>
			<FormDatepicker
				key={publiseringstidspunkt ? 'set' : 'unset'}
				name={`${path}.publiseringstidspunkt`}
				label="Publiseringstidspunkt"
				format={'DD.MM.YYYY HH:mm:ss'}
				disabled={erEksisterendeMelding}
			/>
			{erEndret && (
				<Button
					type="button"
					variant="tertiary"
					size="small"
					icon={<ArrowsCirclepathIcon title="Oppdater rekkefølge" />}
					onClick={handleSort}
					style={{ height: 'fit-content', margin: '25px 15px 0 -10px' }}
				/>
			)}
		</div>
	)
}
