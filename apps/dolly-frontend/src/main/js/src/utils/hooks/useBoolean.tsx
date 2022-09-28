import * as React from 'react'
import { useState } from 'react'

export default (initial: any = false): any => {
	const [isOn, setIsOn] = useState(initial)
	const turnOn = () => setIsOn(true)
	const turnOff = () => setIsOn(false)
	return [isOn, turnOn, turnOff] as const
}
