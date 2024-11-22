const checkEmailExists = async (email) => {
    try {
        const response = await fetch('/api/users/check-email', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email })
        });

        const data = await response.json();
        return data.exists;
    } catch (error) {
        console.error('Error checking email:', error);
        return false; // Return false in case of error
    }
};

export{
    checkEmailExists
}